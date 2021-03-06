;   Copyright (c) Ike Mawira. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns double-snake-game.core
  (:gen-class)
  (:import (java.awt.event KeyEvent ActionListener KeyListener)
           (java.awt Color Dimension)
           (javax.swing JPanel JOptionPane JFrame Timer)))


;; Functional model --------------------------------------------------------------

;; constants
(def field-width 50)
(def field-height 50)
(def point-size 10)
(def turn-millis 50)
(def win-length 50)

;; directions for the snake controlled by the right hand
(def directions
  {KeyEvent/VK_LEFT  [-1 0]
   KeyEvent/VK_RIGHT [1 0]
   KeyEvent/VK_UP    [0 -1]
   KeyEvent/VK_DOWN  [0 1]

   KeyEvent/VK_A     [-1 0]
   KeyEvent/VK_D     [1 0]
   KeyEvent/VK_W     [0 -1]
   KeyEvent/VK_S     [0 1]})

(defn create-snake [side]
  {:body      (if (= side "R") (list [3 0] [2 0] [1 0] [0 0])
                               (list [3 1] [2 1] [1 1] [0 1]))
   :direction (if (= side "R") [1 0]
                               [1 0])
   :type      :snake
   :color     (if (= side "R") (Color. 15 160 70)
                               (Color. 49 120 39))})

(defn create-apple []
  {:location [(rand-int field-width) (rand-int field-height)]
   :color    (Color. 210 50 90)
   :type     :apple})

(defn create-score []
  {:snake-l 0
   :snake-r 0})

(defn point-to-screen-rect [[pt-x pt-y]]
  [(* pt-x point-size) (* pt-y point-size) point-size point-size])

(defn move [{:keys [body direction] :as snake} & grow]
  (assoc snake :body
               (cons (let [[head-x head-y] (first body)
                           [dir-x dir-y] direction]
                       [(+ head-x dir-x) (+ head-y dir-y)])
                     (if grow body (butlast body)))))

(defn increase-score [{:keys [snake-l snake-r] :as score} snake]
  (if (= snake "R")
    (assoc score :snake-r (inc snake-r))
    (assoc score :snake-l (inc snake-l))))

(defn turn [snake direction]
  (assoc snake :direction direction))

(defn win? [{body :body}]
  (>= (count body) win-length))

(defn head-overlaps-body? [head body]
  (contains? (set body) head))

(defn head-outside-bounds? [[head-x head-y]]
  (or
    (> head-x field-height)
    (< head-x 0)
    (> head-y field-height)
    (< head-y 0)))

(defn lose? [{[head & body] :body}]
  (or (head-overlaps-body? head body)
      (head-outside-bounds? head)))

(defn eats? [{[head] :body} {apple :location}]
  (= head apple))

(defn snake-pos [code]
  (if (or (= code KeyEvent/VK_DOWN) (= code KeyEvent/VK_UP)
          (= code KeyEvent/VK_RIGHT) (= code KeyEvent/VK_LEFT))
    "R" "L"))


;; Mutable model -----------------------------------------------------------------

(defn update-positions [snake apple score pos]
  (dosync
    (if (eats? @snake @apple)
      (dosync
        (ref-set score (alter score increase-score pos))
        (ref-set apple (create-apple))
        (alter snake move :grow))
      (alter snake move)))
  nil)

(defn update-direction [snake direction]
  (dosync (alter snake turn direction))
  nil)

(defn reset-game [snake-r snake-l apple score]
  (dosync
    (ref-set score (create-score))
    (ref-set snake-r (create-snake "R"))
    (ref-set snake-l (create-snake "L"))
    (ref-set apple (create-apple)))
  nil)



;; GUI

(defn fill-point [g pt ^Color color]
  (let [[x y width height] (point-to-screen-rect pt)]
    (.setColor g color)
    (.fillRect g x y width height)))

(defmulti paint (fn [g object] (:type object)))

(defmethod paint :apple [g {:keys [location color]}]
  (fill-point g location color))

(defmethod paint :snake [g {:keys [body color]}]
  (doseq [point body]
    (fill-point g point color)))

(defn game-panel [^JFrame frame snake-r snake-l apple score]
  (proxy [JPanel ActionListener KeyListener] []
    ;JPanel
    (paintComponent [g]
      (proxy-super paintComponent g)
      (paint g @apple)
      (paint g @snake-r)
      (paint g @snake-l))
    (getPreferredSize []
      (Dimension. (* (inc field-width) point-size)
                  (* (inc field-height) point-size)))
    ;ActionListener
    (actionPerformed [e]
      (update-positions snake-r apple score "R")
      (update-positions snake-l apple score "L")
      (if (or (lose? @snake-r) (lose? @snake-l))
        (let [ol-score @score]
          (reset-game snake-r snake-l apple score)
          (JOptionPane/showMessageDialog
            frame (str "You lose! Your score, " (+ (:snake-l ol-score) (:snake-r ol-score))))))
      (if (or (win? snake-r) (win? snake-l))
        (let [ol-score @score]
          (reset-game snake-r snake-l apple score)
          (JOptionPane/showMessageDialog
            frame
            (str "You win!, Your score, " (+ (:snake-l ol-score) (:snake-r ol-score))))))
      (.repaint this))
    ;KeyListener
    (keyPressed [e]
      (let [direction (directions (.getKeyCode e))
            snake-d (snake-pos (.getKeyCode e))]
        (if direction
          (update-direction (if (= snake-d "R") snake-r snake-l) direction))))
    (keyReleased [e])
    (keyTyped [e])))



;; Play -------------------------------------------------------------------------

(defn game []
  (let [snake-r (ref (create-snake "R"))
        snake-l (ref (create-snake "L"))
        apple (ref (create-apple))
        score (ref (create-score))
        frame (JFrame. "Snake")
        panel (game-panel frame snake-r snake-l apple score)
        timer (Timer. turn-millis panel)]
    (.setFocusable panel true)
    (.addKeyListener panel panel)

    (.add frame panel)
    (.pack frame)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setVisible frame true)

    (.start timer)))

(defn -main [& args]
  (game))


























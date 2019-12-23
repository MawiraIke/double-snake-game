;   Copyright (c) Ike Mawira. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns double-snake-game.core
  (:import (java.awt.event KeyEvent)
           (java.awt Color)))


;; Functional model --------------------------------------------------------------

;; constants
(def field-width 50)
(def field-height 50)
(def point-size 10)
(def turn-millis 50)
(def win-length 50)

;; directions for the snake controlled by the right hand
(def directions-r
  {KeyEvent/VK_LEFT  [-1 0]
   KeyEvent/VK_RIGHT [1 0]
   KeyEvent/VK_UP    [0 -1]
   KeyEvent/VK_DOWN  [0 1]})

;; directions for the snake controlled by the left hand
(def directions-l
  {KeyEvent/VK_A [-1 0]
   KeyEvent/VK_D [1 0]
   KeyEvent/VK_W [0 -1]
   KeyEvent/VK_S [0 1]})

(defn create-snake [side]
  {:body      (if (= side "R") (list [3 0] [2 0] [1 0] [0 0])
                               (list [0 1] [1 1] [2 1] [3 1]))
   :direction (if (= side "R") [1 0]
                               [-1 0])
   :type      (if (= side "R") :snake_r
                               :snake_l)
   :color     (if (= side "R") (Color. 15 160 70)
                               (Color. 15 160 70))})

(defn create-apple []
  {:location [(rand-int field-width) (rand-int field-height)]
   :color    (Color. 210 50 90)
   :type     :apple})

(defn point-to-screen-rect [[pt-x pt-y]]
  [(* pt-x point-size) (* pt-y point-size) point-size point-size])

(defn move [{:keys [body direction] :as snake} & grow]
  (assoc snake :body
               (cons (let [[head-x head-y] (first body)
                           [dir-x dir-y] direction]
                       [(+ head-x dir-x) (+ head-y dir-y)])
                     (if grow body (butlast body)))))

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
    (> head-x field-width)
    (< head-y 0)))

(defn lose? [{[head & body] :body}]
  (or (head-overlaps-body? head body)
      (head-outside-bounds? head)))

(defn eats? [{[head] :body} {apple :location}]
  (= head apple))


;; Mutable model -----------------------------------------------------------------

(defn update-positions [snake apple]
  (dosync
    (if (eats? @snake @apple)
      (do
        (ref-set apple (create-apple))
        (alter snake move :grow))
      (alter snake move)))
  nil)

(defn update-direction [snake direction]
  (dosync (alter snake turn direction))
  nil)

(defn reset-game [snake-r snake-l apple]
  (dosync
    (ref-set snake-r (create-snake "R"))
    (ref-set snake-l (create-snake "L"))
    (ref-set apple (create-apple)))
  nil)



























### "Snakes"

[![codecov](https://codecov.io/gh/MawiraIke/double-snake-game/branch/master/graph/badge.svg)](https://codecov.io/gh/MawiraIke/double-snake-game)

The snake game, but with two snakes instead.

Enjoy yourself. Set your hair on fire.

Ps: Just for fun, this game was just made to test if this idea is possible to play, and by 
how far.

### Steps to play (with clojure CLI)

run `clj -Sdeps '{:deps {double-snake-game {:git/url "https://github.com/MawiraIke/double-snake-game.git" :sha "42a47c8bdbeb37ca2e6c6bf2e8051bf6f5c656b7"}}}' -m double-snake-game.core`

### Steps to play
If you have git installed, 
1. Clone this repo. If git is not installed, get it [here](https://www.atlassian.com/git/tutorials/install-git) and 
then repeat step one.
2. Assuming you have Leiningen installed run 
```lein run -m double-snake-game.core``` 
from the root of this project else run the jar file in the ```.\target``` folder

Use the arrow and WASD keys to control the snakes.

## License

Copyright © 2019 Ike Mawira

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

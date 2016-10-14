(ns rainbow.game-loop
  (:require [clojure.core.async :refer [<! >! go-loop]]
            [rainbow.game
             :refer
             [check-selected-color create-random-game]]))

(defn start-game-loop [ws-channel]
  (go-loop [game (create-random-game)]
    (>! ws-channel game)

    (when-let [color (:message (<! ws-channel))]
      (recur (check-selected-color game color)))))

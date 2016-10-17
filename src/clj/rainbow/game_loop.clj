(ns rainbow.game-loop
  (:require [chime :refer [chime-ch]]
            [clj-time
             [core :as t]
             [periodic :refer [periodic-seq]]]
            [clojure.core.async :refer [<! >! alts! go-loop]]
            [rainbow.game
             :refer
             [check-selected-color create-game]]))

(defn start-game-loop [ws-channel]
  (go-loop [game (create-game 1 50)
            tick-ch (chime-ch (rest (periodic-seq (t/now) (-> 1 t/seconds))))]
    (>! ws-channel game)

    (when-let [[v ch] (alts! [tick-ch ws-channel])]
      (condp = ch
        tick-ch (recur (update-in game [:time] dec) tick-ch)
        ws-channel (when-let [color (:message v)]
                     (recur (check-selected-color game color) tick-ch))))))

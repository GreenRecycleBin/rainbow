(ns rainbow.game-loop
  (:require [chime :refer [chime-ch]]
            [clj-time
             [core :as t]
             [periodic :refer [periodic-seq]]]
            [clojure.core.async :refer [<! >! go]]
            [rainbow.game :refer [create-random-game]]))

(defn start-game-loop [ws-channel]
  (go
    (let [tick-ch (chime-ch (periodic-seq (t/now)
                                          (-> 3 t/seconds)))]
      (loop [game (create-random-game)]
        (>! ws-channel game)

        (when-let [_ (<! tick-ch)]
          (recur (create-random-game)))))))

(ns rainbow.game
  (:require [clojure.math.combinatorics :as combo]))

(def ^:const color-to-hex {:blue "#0000FF"
                           :green "#00FF00"
                           :indigo "#4B0082"
                           :orange "#FF7F00"
                           :red "#FF0000"
                           :violet "#9400D3"
                           :yellow "#FFFF00"})

(def ^:private ^:const bonus-time 5)

(defn- random-colors [colors n]
  {:pre [(<= n (count colors))]}
  (-> colors
      (combo/combinations n)
      rand-nth
      combo/permutations
      rand-nth))

(defn create-game [n time]
  (let [colors (random-colors (keys color-to-hex) n)]
    {:color-to-hex (select-keys color-to-hex colors)
     :key-color (rand-nth colors)
     :time time
     :colors-count n}))

(defn- create-next-level-random-game [colors-count time]
  (create-game (inc colors-count) time))

(defn- add-bonus-time [time]
  (+ time bonus-time))

(defn check-selected-color [game color]
  (let [{:keys [key-color]} game]
    (if (= key-color color)
      (create-next-level-random-game (:colors-count game) (add-bonus-time (:time game)))
      game)))

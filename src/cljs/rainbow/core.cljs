(ns rainbow.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defonce app-state
  (atom {:ch (ws-ch "ws://localhost:3449/ws")}))

(defn key-color-component [color]
  (om/component
   (dom/div #js {:id "key-color" :className "container"}
            (str/capitalize (name color)))))

(defn send-selected-color [color ch]
  #(go
     (>! ch color)))

(defn color-component [[color hex ch]]
  (om/component
   (dom/div #js {:className (name color)
                 :style #js {:backgroundColor hex}
                 :onClick (send-selected-color color ch)})))

(defn colors-component [[color-to-hex ch]]
  (om/component
   (apply
    dom/div #js {:id "colors" :className "container"}
    (om/build-all color-component (map #(conj % ch) color-to-hex)))))

(defn game-component [app-state owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "board" :className "container"}
               (let [{:keys [key-color]} app-state]
                 (when key-color
                   (om/build key-color-component key-color)))

               (when-let [{:keys [color-to-hex]} app-state]
                 (when color-to-hex
                   (om/build colors-component
                             [(seq color-to-hex) (:ch app-state)])))))))

(defn render-game [container]
  (om/root #'game-component app-state {:target container}))

(defn start-game []
  (render-game (js/document.getElementById "app"))

  (go
    (let [{:keys [ws-channel error]} (<! (:ch @app-state))]
      (when error (throw error))

      (reset! app-state {:ch ws-channel})

      (loop []
        (when-let [game (:message (<! ws-channel))]
          (om/transact! (om/root-cursor app-state) #(merge % game))
          (recur))))))

(defonce game-ch (start-game))

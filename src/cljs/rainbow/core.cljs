(ns rainbow.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state
  (atom {:color-to-hex
         {:yellow "#FFFF00",
          :red "#FF0000",
          :blue "#0000FF",
          :violet "#9400D3",
          :orange "#FF7F00"},
         :key-color :orange}))

(defn key-color-component [color]
  (om/component
   (dom/div #js {:id "key-color" :className "container"}
            (str/capitalize (name color)))))

(defn color-component [[color hex]]
  (om/component
   (dom/div #js {:className (name color)
                 :style #js {:backgroundColor hex}})))

(defn colors-component [color-to-hex]
  (om/component
   (apply
    dom/div #js {:id "colors" :className "container"}
    (om/build-all color-component color-to-hex))))

(defn root-component [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "board" :className "container"}
               (om/build key-color-component (:key-color app))
               (om/build colors-component (:color-to-hex app))))))

(om/root
 root-component
 app-state
 {:target (js/document.getElementById "app")})

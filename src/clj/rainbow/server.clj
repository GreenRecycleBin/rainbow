(ns rainbow.server
  (:gen-class)
  (:require [chord.http-kit :refer [with-channel]]
            [clojure.java.io :as io]
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [resources]]]
            [environ.core :refer [env]]
            [rainbow.game-loop :refer [start-game-loop]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [defaults :refer [api-defaults wrap-defaults]]
             [gzip :refer [wrap-gzip]]
             [logger :refer [wrap-with-logger]]]))

(defn- ws-handler [req]
  (with-channel req ws-channel
    (start-game-loop ws-channel)))

(defroutes routes
  (GET "/ws" [] ws-handler)
  (GET "/" _
       {:status 200
        :headers {"Content-Type" "text/html; charset=utf-8"}
        :body (io/input-stream (io/resource "public/index.html"))})
  (resources "/"))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-with-logger
      wrap-gzip))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (run-jetty http-handler {:port port :join? false})))

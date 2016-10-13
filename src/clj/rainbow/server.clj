(ns rainbow.server
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure
             [core :refer [defroutes GET]]
             [route :refer [resources]]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [defaults :refer [api-defaults wrap-defaults]]
             [gzip :refer [wrap-gzip]]
             [logger :refer [wrap-with-logger]]]))

(defroutes routes
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

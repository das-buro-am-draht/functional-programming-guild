(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(def channel-hub (atom {}))

(defn excludeOwnChannel [hub user-channel]
  (filter (fn [channel-hub-item]
            (not= user-channel channel-hub-item))
          (keys hub)))

(defn chat-handler [request]
  (server/with-channel request user-channel
    (swap! channel-hub assoc user-channel request)
    (server/on-close
     user-channel
     (fn [status]
       (println "channel closed: " status)
       (swap! channel-hub dissoc user-channel)
       (println "channel-hub: " channel-hub)))
    (server/on-receive
     user-channel
     (fn [data]
       (println "channel receive: " data)
       (doseq [friend-channel (excludeOwnChannel @channel-hub user-channel)]
         (println "friend channel: " friend-channel)
         (server/send! friend-channel data))
       (println "channel: " user-channel)))))

(defroutes app-routes
  (GET "/ws" [] chat-handler)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

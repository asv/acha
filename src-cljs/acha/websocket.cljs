(ns acha.websocket
  (:require
    [acha.util :as u]))

(defn resolve-url [url]
  (str "ws://" (.. js/window -location -host) url))

(defn connect [url & {:keys [on-open on-close on-message]}]
  (let [url    (resolve-url url)
        socket (js/WebSocket. url)]
    (when on-open
      (set! (.-onopen socket)
        (fn [event]
          (on-open))))
    (when on-message
      (set! (.-onmessage socket)
        (fn [event]
          (let [data (u/read-transit (.-data event))]
            (if (= :ping data)
              (.send socket (u/write-transit :pong))
              (on-message data))))))
    (when on-close
      (set! (.-onclose socket)
        (fn [event]
          (on-close))))
    socket))

(ns golden.core
  (:require [cheshire.core :as json]
            [clojure.data :as data]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.test :refer [is]]))

(defn update-golden? []
  (= "true" (System/getenv "UPDATE_GOLDEN")))

(defn force-update-golden? []
  (= "true" (System/getenv "FORCE_UPDATE_GOLDEN")))

(defmacro as-json [golden-filename content-gen]
  `(let [actual-content# ~content-gen
         golden-content# (when (.exists (io/file ~golden-filename))
                           (-> (slurp ~golden-filename)
                               (json/parse-string true)))]
     (cond
       (force-update-golden?)
       (do (spit ~golden-filename (json/generate-string actual-content# {:pretty true}))
           (is true))

       (or (nil? golden-content#)
           (and (update-golden?)
                (not= golden-content# actual-content#)))
       (do (spit ~golden-filename (json/generate-string actual-content# {:pretty true}))
           (is true))

       :else
       (is (= golden-content# actual-content#)
           (let [[a# b# both#] (data/diff golden-content# actual-content#)]
             (str "Golden assertion error:\n\n"
                  "Things only in golden (" ~golden-filename "):\n" (with-out-str (pprint/pprint a#)) "\n"
                  "Things only in actual:\n" (with-out-str (pprint/pprint b#)) "\n"
                  "Things in both:\n" (with-out-str (pprint/pprint both#))))))))

(ns golden.core-test
  (:require [golden.core :as golden]
            [clojure.test :refer [deftest is]]))

(deftest vs-json-test
  (golden/as-json "test/assets/foo-1.json"
                  {:foo 1 :bar 2})

  (golden/as-json "test/assets/foo-2.json"
                  {:foo 1 :bar 2})

  (golden/as-jsons ["test/assets/foo-1.json"
                    "test/assets/foo-2.json"]
                   [{:foo 1 :bar 2}
                    {:foo 1 :bar 2}]))

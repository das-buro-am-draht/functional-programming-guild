(ns rest-demo.core-test
  (:require [clojure.test :refer :all]
            [rest-demo.core :refer :all]))

(deftest exclude-own-channel-empty-test
  (testing "exclude-own-channel with empty user-channel"
    (is (= (count (rest-demo.core/excludeOwnChannel {} "user-channel")) 0))))

(deftest exclude-own-channel-filled-test
  (testing "exclude-own-channel with a filled user-channel"
    (is (= (count (rest-demo.core/excludeOwnChannel {'foo 42} "user-channel")) 1))))

(deftest exclude-own-channel-user-channel-test
  (testing "exclude-own-channel with the user-channel"
    (is (= (count (rest-demo.core/excludeOwnChannel {"user-channel" 42} "user-channel")) 0))))

(deftest exclude-own-channel-user-and-friend-channel-test
  (testing "exclude-own-channel with the user-channel and a friend channel"
    (is (= (rest-demo.core/excludeOwnChannel {"user-channel" 42 "friend-channel" 77} "user-channel") (list "friend-channel")))))

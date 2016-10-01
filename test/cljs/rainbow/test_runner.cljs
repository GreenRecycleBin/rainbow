(ns rainbow.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [rainbow.core-test]
   [rainbow.common-test]))

(enable-console-print!)

(doo-tests 'rainbow.core-test
           'rainbow.common-test)

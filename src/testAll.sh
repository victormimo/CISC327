#!/bin/bash
javac FrontEnd.java
for i in {1..39}
do
    echo "running test $i"
    java FrontEnd inputs/activeaccts.txt outputs/$i.txt < inputs/$i.txt > outputs/$i.log
done

for i in {1..39}
do
    echo "checking outputs of test $i"
    diff outputs/$i.txt expected/$i.txt
    diff outputs/$i.log expected/$i.log
done

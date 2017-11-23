#i/bin/bash
javac BackEnd.java
for i in {1..7}
do
    echo "running test $i"
    java BackEnd NEWinputs/$i.txt NEWinputs/MasterAccountFileValid.txt NEWoutputs/accounts$i.txt NEWoutputs/list$i.txt > NEWoutputs/$i.log
done

for i in {1..7}
do
    echo "checking outputs of test $i"
    diff NEWoutputs/Accounts$i.txt NEWexpects/Accounts$i.txt
    diff NEWoutputs/List$i.txt NEWexpects/List$i.txt
    diff NEWoutputs/$i.log NEWexpects/$i.log
done

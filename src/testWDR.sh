#i/bin/bash
javac BackEnd.java
for i in {1..3}
do
    echo "running test $i"
    java BackEnd WDRinputs/$i.txt WDRinputs/MasterAccountFileValid.txt WDRoutputs/accounts$i.txt WDRoutputs/list$i.txt > WDRoutputs/$i.log
done

for i in {1..3}
do
    echo "checking outputs of test $i"
    diff WDRoutputs/Accounts$i.txt WDRexpects/Accounts$i.txt
    diff WDRoutputs/List$i.txt WDRexpects/List$i.txt
    diff WDRoutputs/$i.log WDRexpects/$i.log
done

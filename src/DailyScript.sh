#!/bin/bash
javac FrontEnd.java
for i in {1..3}
do
    echo "Transaction Session $i"
    java FrontEnd ValidAccountList.txt outputsF/$i.txt < Transaction-sessions/$1/$i.txt > outputsF/$i.log
done

for i in {1..3}
do
	echo "Combining Transaction Summary Files"
	cat outputsF/*.txt > MergedTransactionSummaryFile.txt
done

echo 'EOS' >> MergedTransactionSummaryFile.txt

javac BackEnd.java

echo "Running BackEnd"

java BackEnd MergedTransactionSummaryFile.txt MasterAccountFile.txt outputsB/$1/MasterAccountFile.txt ValidAccountList.txt
java BackEnd MergedTransactionSummaryFile.txt MasterAccountFile.txt MasterAccountFile.txt  ValidAccountList.txt
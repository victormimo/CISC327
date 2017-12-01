# Daily Python Script
import os
import dircache
import subprocess

# loop through transaction session folder and select one
dir = 'Transaction-sessions/Day1/'
#path = os.path.join(dir, filename)

def compile_java(java_file):
	cmd = 'javac ' +  java_file
	proc = subprocess.Popen(cmd, shell =True)

def run_java(java_file, valid_acc_list, trans_proc):
	cmd = 'java ' + java_file + " " + valid_acc_list+ " " + dir + trans_proc #>> "outputF/" + trans_proc
	print cmd
	proc = subprocess.Popen(cmd, shell =True)
	proc.wait()

def main():
	valid_acc_list = 'ValidAccountFile.txt'
	for trans_proc in os.listdir(dir):
		# do something
		print trans_proc
		compile_java('FrontEnd.java')
		run_java('FrontEnd', valid_acc_list, trans_proc)
		print 'done'

main()

# Call FrontEnd with Valid account list and Transaction Sessions

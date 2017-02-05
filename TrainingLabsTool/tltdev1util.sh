#!/bin/bash
# helper functions

# Menu Test Mode (comment or uncomment one of the two Modes)
# logheader(){
# 	echo -e "$1: `date` \n---------------------------------------------------------" 
# }
# dopreparation(){
# 	echo "Preparation $1" 

# }
# dosolution(){
# 	echo "Solution $1" 
# }

# doverify(){
# 	[ $verification = true ] && echo "Verification $1"
# }
# End Menu Test Mode 

# Run Ant Mode (comment or uncomment one of the two Modes)
logheader(){
	echo -e "$1: `date` \n------------------------------------------" | tee -a $logfile
}
dopreparation(){
	echo "Preparation $1:" | tee -a $logfile
	{ time ant -f $tltpath/lab_exercises_preparation_dev1.xml $1; } 2>> $logfile

}
dosolution(){
	echo "Solution $1:" | tee -a  $logfile
	{ time ant -f $tltpath/lab_exercises_solution_dev1.xml $1 ; } 2>>  $logfile
}

doverify(){
	echo "Verification $1:" | tee -a $logfile
	[ $verification = true ] && ant executeScript -Dresource=model://"$1" | tee /dev/tty | grep -F 'Congrats! Your solution is valid!' >> $logfile
}
# End Run Ant Mode

doit(){
	currentexercise=$1
	preparationname=$2
	solutionname=$3
	verificationname=$4

	if [[ $exercise -gt ${currentexercise#0} ]] && [[ $upto = true ]] || [[ $exercise -eq ${currentexercise#0} ]]; then 
				logheader "Exercise$currentexercise"
				# no preparation
				if [ $preparationname != NO ]; then 
					dopreparation "Exercise${currentexercise}_$preparationname"
				fi
				if [[ $exercise -gt ${currentexercise#0} ]] && [[ $upto = true ]] || [[ $solution = true ]]; then 
					dosolution "Exercise${currentexercise}_$solutionname"
				fi

				doverify "verifyExercise$currentexercise - $verificationname"
				if [ $exercise -eq ${currentexercise#0} ]; then exit; fi
			fi

}


#locate in ant directory and prepare enviroment
cd ../hybris/bin/platform/
. ./setantenv.sh

#TrainingLabsTool path from hybri/bin/platform 
tltpath=../../../TrainingLabsTool
#DATE=`date +%Y%m%d:%H:%M:%S`
logfile=$tltpath/exercises_timing.txt #_$DATE.txt
rm $logfile

# put the Logger in a redundant place to avoid changing verification scripts
cp $tltpath/resources/exercise1/verification/Logger.groovy ../..

#first parameter is exercise number
exercise=$1

#second parameter is run verification script [default=run preparation or previous preparation/solution cycles if +upto] 
solution=false
if [ ! -x $2 ]  && [ $2 = "+solution" ]; then solution=true; fi

#third parameter is run scripts in a row [default=only run target exercise] 
upto=false
if [ ! -x $3 ]  && [ $3 = "+upto" ]; then upto=true; fi

#forth parameter is run verification script [default=don't run verification] 
verification=false
if [ ! -x $4 ]  && [ $4 = "+verify" ]; then verification=true; fi

echo "trace e:$exercise s:$solution u:$upto v:$verification"	

#Decode exercise number
case $exercise in
1|2|3|4|5|6|7|8|9|10|11)

			# Exercise 1
			# no preparation
			doit "01" "NO" "Installation-solution" "Installation"

			# Exercise 2
			doit "02" "Data_Modeling-prepare" "Data_Modeling-solution" "Data Modeling" 

			# Exercise 3
			doit "03" "Product_Modeling-prepare" "Product_Modeling-solution" "Product Modeling"			

			# Exercise 4
			doit "04" "ImpEx-prepare" "ImpEx-solution" "ImpEx"			

			# Exercise 5
			doit "05" "Flexible_Search-prepare" "Flexible_Search-solution" "Flexible Search"

			# Exercise 6
			doit "06" "Services-prepare" "Services-solution" "Services"

			# Exercise 7
			doit "07" "CommerceServicesFacades-prepare" "CommerceServicesFacades-solution" "Commerce Services and Facades"

			# Exercise 8
			# no preparation
			doit "08" "NO" "Validation-solution" "Validation"

			# Exercise 9
			doit "09" "WCMS-prepare" "WCMS-solution" "WCMS"

			# Exercise 10
			doit "10" "SearchAndNavigation-prepare" "SearchAndNavigation-solution" "Search and Navigation"

			# Exercise 11
			# no preparation
			doit "11" "NO" "Security-solution" "Security"
	
			# End Exercises
			;;
#Help
-h|-H|-help) 
			echo -e "usage: $0 exerciseNumber [+/-solution] [+/-upto] [+/-verify]"
			;;
# Wrong exercise number
*) 
			echo -e "wrong exercise number!\n usage: $0 exerciseNumber [+/-solution] [+/-upto] [+/-verify]"
			;;
esac


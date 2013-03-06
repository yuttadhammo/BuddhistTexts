rm no
for i in `find | grep "./[1-6].*htm"`
	do j=`grep -m 1 "<H2" $i`
	j=`expr "$j" : '.*No\. \([0-9]*\)\..*'`
	if [[ $j ]]
		then
		echo "$j $i" >> no 
		cp $i tmp/ja_e_$j.htm
	fi
	#echo `expr "$i" : '.*j[1-6]\(.*\)'`
done
sort no -g > nos

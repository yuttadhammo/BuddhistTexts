IFS=$'\n\t'
c=-2
for i in `cat mn2.htm`
	do if [[ "$i" =~ "<H2" ]]
		then
			echo "</body></html>" >> mn_e_$c.htm
			(( c += 1 ))
#			cp head.htm mn_e_$c.htm
			rm mn_e_$c.htm
		fi
		echo $i >> mn_e_$c.htm
	done
	

IFS=$'\n\t'
c=0
for i in `cat english.htm`
	do if [[ "$i" =~ "<H2" ]]
		then
			echo "</body></html>" >> dn_e_$c.htm
			(( c += 1 ))
			cp head.htm dn_e_$c.htm
		fi
		echo $i >> dn_e_$c.htm
	done
	

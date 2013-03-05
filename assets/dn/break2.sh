IFS=$'\n\t'
c=0
for i in `cat pali*`
	do if [[ "$i" =~ \<h[1-2]\> ]]
			then
				if [[ $l == 0 ]]
					then
						echo "</body></html>" >> dn_p_$c.htm
						(( c += 1 ))
						cp head.htm dn_p_$c.htm
				fi
				l=1
			else
				l=0
		fi
		echo $i >> dn_p_$c.htm
	done
	
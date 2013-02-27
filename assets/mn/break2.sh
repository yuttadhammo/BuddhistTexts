IFS=$'\n\t'
c=0
for i in `cat pali*.htm`
	do if [[ "$i" =~ \<h[1-3]\> ]]
			then
				if [[ $l == 0 ]]
					then
						echo "</body></html>" >> mn_p_$c.htm
						(( c += 1 ))
						cp head.htm mn_p_$c.htm
				fi
				l=1
			else
				l=0
		fi
		echo $i >> mn_p_$c.htm
	done
	
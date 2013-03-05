IFS=$'\t\n'
for i in `find | grep '_p_'`
	do perl -i -p -0 -e 's/<style>[^<]*<\/style>/<link rel=\"stylesheet\" type=\"text\/css\" href=\"..\/pali.css\">/smg' $i
done
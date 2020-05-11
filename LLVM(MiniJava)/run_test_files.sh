#!/bin/bash
FILE=Main.class
if test -f "$FILE"; then
	for file in $1/*; do
			mkdir -p "outputs";
        	OUTDIR="outputs"
        	OUTNAME=$(basename "${file%.*}")
        	echo -n "$file:   "; java Main $file > "$OUTDIR/$OUTNAME.out";
	done
fi

#!/bin/bash
FILE=Main.class
if test -f "$FILE"; then
	for file in $1/*; do
        	OUTDIR="outputs"
        	OUTNAME=$(basename "${file%.*}")
        	echo -n "$file:   "; java Main $file > "$OUTDIR/$OUTNAME.out";
	done
fi

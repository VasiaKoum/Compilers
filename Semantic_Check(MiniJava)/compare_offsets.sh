#!/bin/bash
for file in $1/*; do
	OUTNAME=$(basename "${file%.*}")
    RIGHTOFF=$(find ./$2/* -name "$OUTNAME.*")
	RIGHTNAME=$(basename "${RIGHTOFF%.*}")
	if [ $OUTNAME = $RIGHTNAME ]
		then
			diff -sB "./$file" "$RIGHTOFF"
	fi
done

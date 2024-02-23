#!/bin/bash

set -e

function createVid() {
	mkdir vid

	cnt=0
	for f in $(echo *.png | sort -n); do
		cp $f ./vid/$cnt.png
		cnt=$((cnt + 1))
	done

	ffmpeg -framerate 2 -pattern_type glob -i './vid/*.png' -c:v libx264 -pix_fmt yuv420p out.mp4
	rm -rf vid
}

mkdir -p videos

for student in $(ls -d screenshots/*); do
	echo $student
	pushd $student
	createVid
	vid_abs=$(readlink -f out.mp4)
	popd
	mv $vid_abs "videos/$(basename $student)"
done

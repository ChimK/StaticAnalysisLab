#!/bin/bash

#INSERT YOUR GIT REPOSITORY BELOW
GIT="https://github.com/apache/commons-math.git"

#INSERT YOUR TARGET CSV FILE BELOW
DATASTORE="../outputs/diffs.csv"

#INSERT THE ROOT DIRECTORY OF THE REPO YOU ARE ANALYSING BELOW
REPO_DIR="commons-math"

#HOW MANY COMMITS DO YOU WANT TO ENCOMPASS IN YOUR ANALYSIS?
NUM_VERSIONS="1000"

# Clone target GIT repository
git clone --single-branch  $GIT

cd $REPO_DIR

# Check out the current head from the git repository
# git checkout master

echo "Timestamp, Message, Committer, LOC" > $DATASTORE

versions=($(git log master --no-walk --tags --pretty=format:"%h" -$NUM_VERSIONS ))

num=${#versions[@]}

for j in $(seq 1 $num); do

    i=${versions[$j]}

    git checkout -f $i

    next=$(($j + 1))
    nextver=${versions[$next]}

	DATE=$(git show -s --format='%ct' $i)

	MESSAGE=$(git show -s --format='%B' $i | tr -d '[:punct:]\r\n')

	AUTHOR=$(git show -s --format='%an' $i)

	LOC=$(find src -name '*.java' | xargs wc -l | awk {'print $1'} | tail -n1)

	echo "\"$DATE\", \"$MESSAGE\", \"$AUTHOR\", $LOC" >> $DATASTORE

done

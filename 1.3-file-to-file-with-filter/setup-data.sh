#!/bin/bash
echo 'Preparing test folder:'
echo '  Cleaning test folder...'
rm -rf origin
rm -rf destination
mkdir -p origin
mkdir -p destination
echo '  Copying sample data files...'
cp ../data/orders/* origin
echo 'Preparation complete!'

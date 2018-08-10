#!/bin/bash
echo 'Preparing test folder:'
echo '  Cleaning test folder...'
rm -rf orders
mkdir -p orders/incoming
echo '  Copying sample data files...'
cp ../data/orders2/* orders/incoming
echo 'Preparation complete!'

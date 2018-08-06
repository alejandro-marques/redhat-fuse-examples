#!/bin/bash

rm -fr orders
mkdir -p orders/incoming

cp -a ../data/orders/* orders/incoming

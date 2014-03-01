#!/bin/bash

ssh 51b8b47c4382ec3cf90002c3@honeybee-otika.rhcloud.com "bash app-root/data/dumper.sh"
scp 51b8b47c4382ec3cf90002c3@honeybee-otika.rhcloud.com $HOME/Dropbox/HoneyBee/

exit 0

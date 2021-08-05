# the important bits are calling out the replicaset, binding the IP to 0.0.0.0, and setting the oplogSize to something manageable
# these settings are for local development only, and would likely be significantly different in a production deployment
mongod --config /usr/local/etc/mongod.conf --fork --replSet rs0 --bind_ip 0.0.0.0 --oplogSize 100
# mongosh replica-init.js
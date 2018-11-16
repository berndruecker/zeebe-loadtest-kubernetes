# zeebe-loadtest-kubernetes
Load tests for Zeebe which can be run on Kubernetes using Helm charts to provision everything automatically.

!(setup.png "Load Test Setup")

## Running this on Google Cloud

You should be able to run in on every Kubernetes environment, I currently run it on Google Cloud by:

* Create the cluster:
```
gcloud container clusters create zeebe-load --num-nodes 8 --machine-type=n1-standard-2
```

Other machine types: n1-highmem-2, n1-standard-2, n1-highcpu-2

* Create Zeebe cluster (services go first!)

```
cd k8s-config-map
kubectl apply -f zeebe-cluster-service.yaml
kubectl apply -f zeebe-gateway-service.yaml
kubectl apply -f zeebe.yaml
```

* Create load / workers (do not start before Zeebe is up)

```
cd k8s-config-map
kubectl apply -f zeebe-cluster-service.yaml
kubectl apply -f zeebe-gateway-service.yaml
kubectl apply -f zeebe.yaml
```

* Delete cluster once you are finished

```
gcloud container clusters delete zeebe-load
```

## Some commands that might be helpful

* Bash into Zeebe pod:

```
kubectl exec -it zeebe-0 -- /bin/bash
```

* Show Zeebe cluster topolgy (on Zeeeb pod):
```
ZB_BROKER_ADDR=zeebe-0.zeebe:26500 /usr/local/zeebe/bin/zbctl status
```

* Show log of Zeebe
```
tail -f /usr/local/zeebe/logs/zeebe.log
```

* Copy Zeebe log to your machine
```
kubectl cp zeebe-0:/usr/local/zeebe/logs/zeebe.log logs
```

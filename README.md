# Vertigo AI Project

The Vertigo AI Projet aims to provide a standard and open source machine learning module to the Vertigo Framework. The project is divided into three parts:

- the backend server, which exposes scoring, training and prediction functionalities through API.
- the Vertigo module, which provides a set of connectors to the backend server and to training databases.
- a UI, which can be deployed along side with the backend server to ease machine learning model versioning and monitoring.

## Try it locally
You can use the backend server APIs and the UI locally by building and running their docker containers. To do so, ensure Docker is installed and execute the following commands at the project root :

```bash
> docker compose build
> docker compose up
```

## Documentation

### Bundle
The bundles are standard exchange format between the project's component. It exists in two forms, which use the json format:

-  a pre-trained form which contains the necessary information to train the model
- a trained form which contains the pre-trained form along side with preprocessing, explanation and training information. This form is usually zipped with a binary trained machine learning model. 

The following examples show the two form of a same bundle using the [Iris Dataset](https://archive.ics.uci.edu/ml/datasets/iris)

#### Pre-trained bundle
```json
{
  "meta": {
    "name": "iris",
    "version": 0,
    "language": "python",
    "framework": "scikit-learn",
    "datasource": "https://archive.ics.uci.edu/ml/datasets/iris",
    "description": "Iris variety predictor using the length and the width of the sepals and of the petals.",
    "status": "trained"
  },
  "algorithm": {
    "package": "sklearn.tree",
    "name": "DecisionTreeClassifier",
    "score": {
      "scoreMean": 0.9600000000000002,
      "scoreStd": 0.03265986323710903
    }
  },
  "dataset": {
    "db_config": {
      "key": "postgresql",
      "table": "iris",
      "database": "traindb"
    },
    "data_config": [
      {
        "id": 0,
        "name": "sepal_length",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 1,
        "name": "sepal_width",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 2,
        "name": "petal_length",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 3,
        "name": "petal_width",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 4,
        "name": "variety",
        "domain": "String",
        "is_label": true,
        "ifna": null,
        "preprocessing": ""
      }
    ],
    "domains": {
      "String": "str",
      "Float": "float",
      "Integer": "int",
      "Boolean": "bool"
    }
  },
  "parameters": {
    "n_jobs": 1,
    "cv": 5,
    "metrics": "accuracy",
    "grid_search": false,
    "hyperparameters": {}
  }
}
```

#### Trained bundle

```json
{
  "meta": {
    "name": "iris",
    "version": 0,
    "language": "python",
    "framework": "scikit-learn",
    "datasource": "https://archive.ics.uci.edu/ml/datasets/iris",
    "description": "Iris variety predictor using the length and the width of the sepals and of the petals.",
    "status": "trained"
  },
  "algorithm": {
    "package": "sklearn.tree",
    "name": "DecisionTreeClassifier",
    "score": {
      "scoreMean": 0.9600000000000002,
      "scoreStd": 0.03265986323710903
    }
  },
  "dataset": {
    "db_config": {
      "key": "postgresql",
      "table": "iris",
      "database": "traindb"
    },
    "data_config": [
      {
        "id": 0,
        "name": "sepal_length",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 1,
        "name": "sepal_width",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 2,
        "name": "petal_length",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 3,
        "name": "petal_width",
        "domain": "Float",
        "is_label": false,
        "ifna": "0.0",
        "preprocessing": "StandardScaler"
      },
      {
        "id": 4,
        "name": "variety",
        "domain": "String",
        "is_label": true,
        "ifna": null,
        "preprocessing": ""
      }
    ],
    "domains": {
      "String": "str",
      "Float": "float",
      "Integer": "int",
      "Boolean": "bool"
    }
  },
  "parameters": {
    "n_jobs": 1,
    "cv": 5,
    "metrics": "accuracy",
    "grid_search": false,
    "hyperparameters": {}
  },
  "preprocessing": {
    "sepal_length": {
      "StandardScaler": {
        "mean": 5.843333333333334,
        "var": 0.6811222222222223,
        "scale": 0.8253012917851409
      }
    },
    "sepal_width": {
      "StandardScaler": {
        "mean": 3.0573333333333337,
        "var": 0.1887128888888889,
        "scale": 0.4344109677354946
      }
    },
    "petal_length": {
      "StandardScaler": {
        "mean": 3.7580000000000005,
        "var": 3.0955026666666665,
        "scale": 1.759404065775303
      }
    },
    "petal_width": {
      "StandardScaler": {
        "mean": 1.1993333333333336,
        "var": 0.5771328888888888,
        "scale": 0.7596926279021594
      }
    }
  },
  "training_parameters": {
    "estimator": {
      "ccp_alpha": 0,
      "class_weight": null,
      "criterion": "gini",
      "max_depth": null,
      "max_features": null,
      "max_leaf_nodes": null,
      "min_impurity_decrease": 0,
      "min_impurity_split": null,
      "min_samples_leaf": 1,
      "min_samples_split": 2,
      "min_weight_fraction_leaf": 0,
      "random_state": null,
      "splitter": "best"
    }
  },
  "training_data": {
    "time": 1.9264631271362305,
    "x_shape": [
      150,
      4
    ],
    "y_shape": [
      150,
      1
    ]
  },
  "explanation": {
    "feature": [
      "petal_width",
      "petal_length",
      "sepal_width",
      "sepal_length"
    ],
    "importance": [
      1.037600000000003,
      0.357837037037037,
      0.07620740740740758,
      0
    ]
  },
  "major_parameters": {
    "Depth": "None",
    "Samples": 2,
    "Criterion": "gini"
  }
}

```
### Server API endpoint

#### Get bundle list
```
> GET /api/bundles/
[
    {
        "name": "bundle_name",
        "performance": 37.0 ,
        "use": 123,
        "active": true,
        "activeVersion": 1,
        "versions": [0, 1, 3],
        "scores": [0.35, 0.55, 0.51],
        "bestVersion": 1,
        "description": "description of your data",
        "datasource": "http://url/of/your/data/"                    
    }
]
```

#### Get bundle's versions list
```
> GET /api/bundle/<str:bundle>/
{
    "bundle":
    {
        "name": "bundle_name",
        "performance": 37.0 ,
        "use": 123,
        "active": true,
        "activeVersion": 1,
        "versions": [0, 1, 3],
        "scores": [0.35, 0.55, 0.51],
        "bestVersion": 1,
        "description": "description of your data",
        "datasource": "http://url/of/your/data/"                   
    },
    "versions:
    [
        {
            "name":"bundle_name",
            "version":0,
            "status":"trained",
            "score":0.35
        },
        {
            "name":"bundle_name",
            "version":1,
            "status":"active",
            "score":0.55
        },
        {
            "name":"bundle_name",
            "version":3,
            "status":"trained",
            "score":0.53
        },
    ]
}
```

#### Get bundle versions 
```
> GET /api/bundle/<str:bundle>/<int:version>/
bundle.json
```

#### Remove bundle
```
> DELETE api/remove/<str:bundle>/
HTTP 204 NO CONTENT
```

#### Remove version
```
> DELETE api/remove/<str:bundle>/<int:version>/
HTTP 204 NO CONTENT
```

#### Download bundle
```
> GET api/download/<str:bundle>/
```

#### Download version
```
> GET api/download/<str:bundle>/<int:version>/
```

#### Train a new bundle version
```
> POST api/deploy-train/ --data=bundle.json
{
    "time": seconds,
    "modelName": str,
    "version": int,
    "score": str,
    "status": [active, deployed],
    "response": HTTP response
}
``` 
#### Retrain a bundle version
```
> POST api/train/<str:name>/<int:version>/
{
    "time": seconds,
    "modelName": str,
    "version": int,
    "score": str,
    "status": active or deployed,
    "response": HTTP response
}
```

#### Score a bundle version
```
> GET api/score/<str:name>/<int:version>/
{
    "time": seconds,
    "modelName": str,
    "version": int,
    "score": str,
    "status": active or deployed,
    "response": HTTP response
}
``` 

#### Deploy a trained bundle version
```
> POST api/deploy/ --file=bundle.zip
```

#### Activate a bundle version
```
> PUT api/activate/<str:bundle>/<int:version>/
```

#### Predict
```
> POST api/predict/<str:bundle>/<int:version>/ --data=data
{
    "predictionLabel": str,
    "predictionNumeric": int or float,
    "predictionVector": list,
    "explanation1D": list,
    "explanation2D": 2d array
}
```

### Vertigo AI Module

See the javadoc.
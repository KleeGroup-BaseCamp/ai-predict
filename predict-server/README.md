# AIPredict server

The AIPredict server is a solution to remotely train your machine learning models and deploy them to a server that provides restful APIs to make data predictions using these models. This solutions relies on two webservices : the Model Training Service and the Data Prediction Service.

The Model Training Service trains and scores your machine learning model on a remote server. It saves the models and exports them to the Data Prediction Service.

The Data Prediction Service is independent from the Model Training service. It stores trained models either from the Model Training Service or from any other sources.
## Quickstart

### Server deployment
To start the server locally, you first need to create a Python 3.6 virtual environment:
```bash
$ virtualenv ./env --python=python3.6
```
Activate the new virtual environment and install all the server's dependencies:
```bash
$ pip install -r requirements.txt
```
If you run the server for the first time, apply the migrations to the database with these two commands:
```bash
$ python scripts/manage.py makemigrations
$ python scripts/manage.py makemigrations AIPredict
$ python scripts/manage.py migrate
```
Then run the server :
```bash
$ python scripts/manage.py runserver
```
You should be able to access the server locally on http://127.0.0.1:8000 .

## Building a bundle
Most of the AIPredict server's functionality rely on a json file, hereinafter referred as **bundle.json** or **config.json**. This file contains all the information the server need to execute training and prediction request and thus, it must be properly configured to avoid errors. Here is an example of how to write this file:

```json config.json
{
    "meta": 
    {
        "name": "iris-classification-postgresql",
        "version": 0,
        "language": "python",
        "framework": "scikit-learn",
        "URLDataSources": "",
        "SearchLoaderID": "",
        "secret": ""
    },
    "algorithm": 
    {
        "package": "sklearn.tree",
        "name": "DecisionTreeClassifier",
        "type" : "Classification",
    },
    "dataset":
    {
        "db_config":
         {
             "db_type": "postgresql",
             "table": "iris",
             "database": "traindb",
             "user": "postgres",
             "password": "admin",
             "host": "localhost",
             "port": "5432"
         },
        "data_config":
        {
            "sepalLength": {"domain": "Float", "is_label":false, "ifna":0.0},
            "sepalWidth": {"domain": "Float", "is_label":false, "ifna":0.0},
            "petalLength": {"domain": "Float", "is_label":false, "ifna":0.0},
            "petalWidth": {"domain": "Float", "is_label":false, "ifna":0.0},
            "variety": {"domain": "Label", "is_label":true}
        },
            "domains":
        {
            "Float": "float",
            "Label": "str"
        },
    },
    "parameters":
    {
        "n_jobs": 1,
        "cv": 5,
        "metrics": "accuracy",
        "grid_search": true,
        "min_score": 0.0,
        "hyperparameters":
        {
            "max_depth": [1, 10, 100, 1000, 10000],
            "random_state": [0, 1]
        },
    },
    "preprocessing":
    {
        {
            "variety": 
            {
                "LabelEncoder": {"classes": [["setosa", "virginica", "versicolor"]]}},
                "sepalLength": 
                {
                    "StandardScaler": 
                    {
                        "mean": 5.843333333333334,
                        "var": 0.6811222222222223,
                        "scale": 0.8253012917851409
                    }
                },
                "sepalWidth": 
                {
                    "StandardScaler": 
                    {
                        "mean": 3.0573333333333337,
                        "var": 0.1887128888888889,
                        "scale": 0.4344109677354946
                    }
                },
                "petalLength": 
                {
                    "StandardScaler": 
                    {
                        "mean": 3.7580000000000005,
                        "var": 3.0955026666666665,
                        "scale": 1.759404065775303}},
                "petalWidth": 
                {
                    "StandardScaler":
                    {
                        "mean": 1.1993333333333336,
                        "var": 0.5771328888888888,
                        "scale": 0.7596926279021594
                    }
                }
        }
    }
}
```
This example uses the [Iris Dataset]("https://archive.ics.uci.edu/ml/datasets/iris") and the [DecisionTreeClassifier](https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html) from scikit-learn to classify the iris by variety.  Please notice this several points :

- The *meta* field must contains a name for the model to train/predict. Only alphanumeric characters and dashes can be use. The name and the version (an integer) of the model must be unique together because they constitute the key to retrieve the model once imported.
- The *algorithm* field refers to the model algorithm by giving the exact module name and package (here ``DecisionTreeClassifier`` and ``sklearn.tree``)
- Three items are required in the *dataset* field : the database configuration *db_config*, the dataset scheme *data_config* and the data domains *domains*. 
    - The *db_config* gives all the details required by the server to access the training data. The field *db_type* is always required to indicates which type of database is used (refers to this section to see the compatible solutions).
    - The *data_config* field indicates how the dataset is built by naming and typing the data fields, making the distinction between feature and label and by providing a value if a feature is empty (can be set as ``"_required"`` also)
    - The *domains* is the data typing dictionary to make the link between your application types and python.
- The *parameters* field contains all the required information for training. Four fields are required : a *metric*, a minimal score to allow the model deployment *min_score* and the *hyperparameters* dictionary which could be configured either for grid search or not as it is indicated in the boolean *grid_search*.
- Finally, the *preprocessing* dictionary contains all the information needed for feature engineering. It can be an empty dictionary. It can be manually edited or can be obtained using the [FeatureEngineering]("./AIPredict/apps/predict/utils/preprocessing.py#L4") class.

The input json structure is the same for the Model Training Service and the Data Prediction Service. Although, there is still WIP to allow a lighten version of the *bundle.json* through the prediction endpoints.

## Model Training Service

### Model Training
The main functionality of the Model Training Service is of course training. To do so, you need to provide a json file, hereinafter referred as **config.json**, to the server through a Restful POST request to the endpoint ```/deploy-train```. This json must respect the structure given above.

The training can be divided in five steps :
1. The server extracts the data from the database
2. The model is set up and trained with the training data. This step can take quite some time, thus it is realized asynchronously to not block the execution of other requests.
3. The trained model is scored. This step is also asynchronous.
4. If the model score is higher than the minimum score filled in the **config.json**, the model is saved. Else the server send a HTTP Response 200.
5. When the model is saved, the Model Train Service try to deploy it to the Data Prediction Service. Whatever the result of the deployment request, respond with a HTTP Response 201.

The training data have to be store in a database and the connection information must be provided in the *db_config* field. The compatible database are :
- Postgresql


</table>

### Model Re-Training
Once a model is saved, it is possible to retrain it when new data are added to the training database. To do so, use the POST ``/train/<str:name>/<int:version>`` endpoint. The request execution is similar to the first training detailed above. A new version of the model is saved if the new score is great enough.

### Model Scoring
The Model Training Service also provide an endpoint to score a saved model. Thus, when new data are available on the database, a request to the POST ``/score/<str:name>/<int:version>`` endpoint return the model score on the new data.

### Model Deletion
The Model Training Service provides an endpoint to delete a given saved model with :``/delete-train/<str:name>/<int:version>``. It returns a HTTP Response 204 if succeeded.

## Data Prediction Service

### POST ```/deploy```
To upload a model on the prediction server, you should send a zip archive on the server using the POST ```/deploy``` API endpoint. The zip archive must contains two files :

- a binary file called **model.pkl** containing a serialized Python trained model. Consider using the module [pickle]("https://docs.python.org/3/library/pickle.html") to serialize your objects. To see compatible model algorithm, refers to [Supported Models](#supported_models)
- a json file, **bundle.json**, which respect the structure given above.

If the request succeed, the server returns a response 201.

```
POST /deploy/ HTTP/1.1
Host: 127.0.0.1:8000
Cache-Control: no-cache

----WebKitFormBoundaryE19zNvXGzXaLvS5C
Content-Disposition: form-data; name="archive"; filename="test_bundle.zip"
Content-Type: application/x-zip-compressed


----WebKitFormBoundaryE19zNvXGzXaLvS5C
Content-Disposition: form-data; name="archive"


----WebKitFormBoundaryE19zNvXGzXaLvS5C
```

### PUT ```/activate/<str:bundle>/<int:version>/```
Once the bundle is imported, you need to activate it using the name and the version provided in the **bundle.json** file. Only models from activated bundles can be used for prediction.

The activation of one version of a bundle deactivates all the other version of this bundle only. Thus, several bundles can be active simultaneously but only one version of each can be used for prediction.

To activate a version of a bundle, simply write a Restful PUT request to the server endpoint ```/activate/<str:bundle>/<int:version>``` where *bundle* stands for the bundle name.

### DELETE ```/delete/<str:bundle>/<int:version>```
You can delete any imported version of a bundle, even the activated one. When the server receives a delete request on the ```/delete/<str:bundle>/<int:version>``` endpoint, it removes the bundle record in the database and all files linked the requested bundle version.
### GET ```/model-bundles```
You can access the list of all imported bundles by doing a GET request on ```/model-bundles```. The response respects the following format :
```json
[
    {
        "id": 1,
        "name": "bundle_name",
        "path": "./bundles/bundle_name/v1",
        "activated": true,
        "version": 1
    },
    ...
]
```
### POST ```/predict/<str:bundle>/<int:version>```
You can use this endpoint to make a predict using a model from an activated bundle version. To do so, you need to write a POST request on ```/predict/<str:bundle>/<int:version>``` and add your data in the request body using on of the following structure:
```json
[
    {
        "StringExample" : "blue",
        "NumericalExample": 9
    },
    {
        "StringExample" : "red",
        "NumericalExample": 12
    }
]
```
```json
{
        "StringExample" : {"1" : "blue", "2" : "red"},
        "NumericalExample": {"1" : 9, "2" : 12}
}
```
```json
{
        "StringExample" : ["blue", "red"],
        "NumericalExample": [9, 12]
}
```

If the prediction succeeds, the server send a response 200 and a json with the following structure:
```json
[
    {
        "predictionNum" : 2,
        "predictionStr" : null,
        "predictionProba" : [0.2, 0.3, 0.5], // null if the model is not a classifier
        "explanation": [[-0.032, 1.38E-4, -0.041], [0.068, -0.046, 0.0517]] // null if the model is not a tree-based model classifier, else array of shape class_number x feature_number
    },
    {
        "predictionNum" : 0,
        "predictionStr" : null,
        "predictionProba" : [0.8, 0.05, 0.15],
        "explanation": [[0.232, -0.568, -0.156], [0.081, -0.325, -0.145]] 
    },
]
```
### <a name="#supported_models"></a>Supported models

<table>
    <thead>
        <tr>
            <th>Librairy</th>
            <th>Type</th>
            <th>Model</th>
            <th>Explanation</th>
        </tr>
    </thead>
    <tbody style="text-align: center; vertical-align: middle;">
        <tr>
            <td rowspan=19>scikit-learn</td>
            <td rowspan=11>Classifier</td>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeClassifier.html">DecisionTreeClassifier</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.RandomForestClassifier.html">RandomForestClassifier</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.IsolationForest.html">IsolationForest</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.svm.SVC.html">SVC</a></td>
            <td></td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.neighbors.KNeighborsClassifier.html">KNeighborsClassifier</a></td>
            <td></td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LogisticRegression.html">LogisticRegression</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.discriminant_analysis.LinearDiscriminantAnalysis.html">LinearDiscriminantAnalysis</a></td>
            <td> </td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.discriminant_analysis.QuadraticDiscriminantAnalysis.html">QuadratricDiscriminantAnalysis</a></td>
            <td> </td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.ExtraTreesClassifier.html">ExtraTreesClassifier</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.GradientBoostingClassifier.html">GradientBoostingClassifier</a></td>
            <td> </td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.neural_network.MLPClassifier.html">MLPClassifier</a></td>
            <td> </td>
        </tr>
        <tr>
            <td rowspan=7>Regression</td>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LinearRegression.html?highlight=linear#sklearn-linear-model-linearregression">LinearRegression</a></td>
            <td> </td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.RandomForestRegressor.html">RandomForestRegressor</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.svm.SVR.html">SVR</a></td>
            <td></td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.tree.DecisionTreeRegressor.html">DecisionTreeRegressor</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.ExtraTreesRegressor.html">ExtraTreesRegressor</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.ensemble.GradientBoostingRegressor.html">GradientBoostingRegressor</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.neural_network.MLPRegressor.html">MLPRegressor</a></td>
            <td> </td>
        </tr>
        <tr>
            <td rowspan=1>Clustering</td>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.cluster.KMeans.html">KMeans</a></td>
            <td> </td>
        </tr>
        <tr>
            <td rowspan=2>XGBoost</td>
            <td rowspan=1>Classification</td>
            <td><a href="https://xgboost.readthedocs.io/en/latest/python/python_api.html#xgboost.XGBClassifier">XGBClassifier</a></td>
            <td>&#10003</td>
        </tr>
        <tr>
            <td rowspan=1>Regression</td>
            <td ><a href="https://xgboost.readthedocs.io/en/latest/python/python_api.html#xgboost.XGBRegressor">XGBRegressor</a></td>
            <td>&#10003</td>
        </tr>
    </tbody>
</table>
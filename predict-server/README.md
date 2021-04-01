# Prediction server

## Quickstart

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
$ python scripts/manage.py migrate
```
Then run the server :
```bash
$ python scripts/manage.py runserver
```
You should be able to access the server locally on http://127.0.0.1:8000 .
## Endpoints

### POST ```/deploy```
To upload a model on the prediction server, you should send a zip archive on the server using the POST ```/deploy``` API endpoint. The zip archive must contains two files :

- a binary file called **model.pkl** containing a serialized Python trained model. Consider using the module [pickle]("https://docs.python.org/3/library/pickle.html") to serialize your objects. To see compatible model algorithm, refers to [Supported Models](#supported_models)
- a json file, **bundle.json**, with the following structure 

    ```json bundle.json
    {
        "meta": 
        {
            "name": "flags",
            "version": 1,
            "language": "python",
            "framework": "scikit-learn",
            "URLDataSources": "",
            "SearchLoaderID": "",
            "secret": ""
        },
        "algorithm": 
        {
            "name": "RandomForest",
            "type" : "Classifier",
            "metrics": ""
        },
        "data":
        {
            "StringExample": {"domain": "DoLabel", "is_label":false}
        },
        "domains":
        {
            "DoLabel": "String",
        },
        "preprocessing":
        {
            "StringExample": 
            {
                "OneHotEncoder": 
                {
                    "categories": [["black", "blue", "gold", "green", "orange", "red", "white"]],
                    "drop_idx": null
                }
            },
            "NumericalExample": 
            {
                "StandardScaler":
                {
                    "mean": 10,
                    "var": 2,
                    "scale": 4.23
                }
            }
        }
    }
    ```
    The name and the version of a bundle must be unique together. They are the identifiers you need to interact with the model once the bundle is imported.

    The preprocessing dictionary can be manually edited or can be obtained using the [FeatureEngineering]("./AIPredict/apps/predict/utils/preprocessing.py#L4") class.

If the request succeed, the server returns a response 201.
### PUT ```/activate/<str:bundle>/<int:version>```
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
## <a name="#supported_models"></a>Supported models

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
            <td rowspan=11>scikit-learn</td>
            <td rowspan=5>Classifier</td>
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
            <td rowspan=5>Regression</td>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LinearRegression.html?highlight=linear#sklearn-linear-model-linearregression">LinearRegression</a></td>
            <td> </td>
        </tr>
        <tr>
            <td ><a href="https://scikit-learn.org/stable/modules/generated/sklearn.linear_model.LogisticRegression.html">LogisticRegression</a></td>
            <td>&#10003</td>
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
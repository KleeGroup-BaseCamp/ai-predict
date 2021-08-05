import numpy as np

MAJOR_PARAMETERS_MAP = {
    "sklearn.tree.DecisionTreeClassifier": {"max_depth": "Depth", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.ensemble.RandomForestClassifier" : {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.ensemble.IsolationForest": {"n_estimators": "Trees", "max_samples": "Max samples", "contamination": "Contamination"},
    "sklearn.svm.SVC": {"C": "C", "kernel": "Kernel", "degree": "Degree", "gamma": "Gamma"},
    "sklearn.neighbors.KNeighborsClassifier" : {"n_neighbors": "Neighbors"},
    "sklearn.linear_model.LogisticRegression": {"penalty": "Penality", "C":"C"},
    "sklearn.discriminant_analysis.LinearDiscriminantAnalysis": {"solver":"Solver", "n_components": "Components", "shrinkage":"Shrinkage"},
    "sklearn.discriminant_analysis.QuadraticDiscriminantAnalysis": {"reg_param":"Regularization"},
    "sklearn.ensemble.ExtraTreesClassifier": {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.ensemble.GradientBoostingClassifier": {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion", "learning_rate": "Learning rate"},
    "sklearn.neural_network.MLPClassifier": {"hidden_layer_sizes": "Hidden Layers", "activation": "Activation", "alpha": "Alpha", "solver": "Solver"},
    "sklearn.linear_model.LinearRegression" : {},
    "sklearn.tree.DecisionTreeRegressor": {"max_depth": "Depth", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.ensemble.RandomForestRegressor": {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.svm.SVR": {"C": "C", "kernel": "Kernel", "degree": "Degree", "gamma": "Gamma"},
    "sklearn.ensemble.ExtraTreesRegressor": {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion"},
    "sklearn.neural_network.MLPRegressor": {"hidden_layer_sizes": "Hidden Layers", "activation": "Activation", "alpha": "Alpha", "solver": "Solver"},
    "sklearn.ensemble.GradientBoostingRegressor": {"max_depth": "Depth", "n_estimators": "Trees", "min_samples_split": "Samples", "criterion": "Criterion", "learning_rate": "Learning rate"},
    "sklearn.cluster.KMeans": {"n_clusters": "Clusters", "n_init": "Seeds", "max_iter": "Max Iterations"},
    "xgboost.XGBClassifier": {"max_depth": "Depth", "n_estimators": "Trees", "learning_rate": "Learning rate"},
    "xgboost.XGBRegressor": {"max_depth": "Depth", "n_estimators": "Trees", "learning_rate": "Learning rate"},
}

def get_major_parameters(package:str, modelName:str, model, grid_search:bool):
    name = ".".join([package, modelName])
    major_parameters = MAJOR_PARAMETERS_MAP[name]
    model_parameters = model.get_params()
    if grid_search:
        key = "estimator__"
    else:
        key = ""
    res = {}
    for param in major_parameters:
        print(model_parameters)
        value = model_parameters[key+param]
        if not value:
            res[major_parameters[param]] = "None"
        elif isinstance(value, float):
            res[major_parameters[param]] = np.round(value, 3)
        else:
            res[major_parameters[param]] = value
    return res
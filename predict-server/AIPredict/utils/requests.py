

def camel_case_to_snake_case(key):
    return ''.join(['_' + c.lower() if c.isupper() else c for c in key]).lstrip('_')

def convert_request_data(l_request_data_cc):

    l_request_data_sc = []
    for request_data_cc in l_request_data_cc:
        request_data_sc = {}
        for key in request_data_cc:
            request_data_sc[camel_case_to_snake_case(key)] = request_data_cc[key]
        l_request_data_sc.append(request_data_sc)
    return l_request_data_sc


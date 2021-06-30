<template>
  <apexchart
    type="bar"
    height="100%"
    :options="categories()"
    :series="series()"
  />
</template>

<script>
export default {
  name: "FeatureImportancesChart",
  data() {
    return {
      chartOptions: {
        colors: ["#264653"],
        chart: {
          align: "top",
          type: "bar",
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            borderRadius: 4,
            horizontal: true
          }
        },
        dataLabels: {
          enabled: false
        },
        xaxis: {
          categories: [],
          labels: {
            show: false
          }
        }
      }
    };
  },
  methods: {
    series() {
      var importance = this.explanation.importance;
      const sum = importance.reduce((a, b) => a + b);
      var importance_percent = [];
      importance.forEach(imp =>
        importance_percent.push((100 * (imp / sum)).toPrecision(5))
      );
      if (importance.length <= 5) {
        return [{ name: "Feature importance (%)", data: importance_percent }];
      } else {
        var major_importance = importance_percent.slice(0, 4);
        major_importance.push(
          importance_percent
            .slice(4, importance_percent.length)
            .reduce((a, b) => a + b)
        );
        return [{ name: "Feature importance (%)", data: major_importance }];
      }
    },
    categories() {
      var options = this.chartOptions;
      if (this.explanation.feature.length < 5) {
        options.xaxis.categories = this.explanation.feature;
      } else {
        options.xaxis.categories = this.explanation.feature.slice(0, 4);
        options.xaxis.categories.push("Other features");
      }
      return options;
    }
  },
  props: {
    explanation: {
      type: Object,
      required: true
    }
  }
};
</script>

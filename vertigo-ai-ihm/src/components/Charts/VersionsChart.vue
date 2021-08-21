<template>
  <apexchart
    class="bg-primary score-chart"
    type="line"
    height="100%"
    width="100%"
    :options="options()"
    :series="series()"
  />
</template>

<script>
import "apexcharts";
export default {
  name: "VersionsChart",
  props: {
    name: {
      type: String,
      required: true
    },
    data: {
      type: Array,
      required: true
    },
    versions: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      chartOptions: {
        colors: ["#FB8C00"],
        chart: {
          height: 350,
          type: "line",
          foreColor: "#FFFFFF",
          zoom: {
            enabled: false
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: "straight",
          width: 2
        },
        markers: {
          hover: {
            sizeOffset: 4
          }
        },
        yaxis: {
          title: {
            text: "Scores"
          }
        },
        xaxis: {
          labels: {
            rotate: -45
          },
          categories: []
        }
      }
    };
  },

  methods: {
    options() {
      var categories = [];
      for (var i = 0; i < this.versions.length; i++) {
        categories.push("Version".concat(" ", this.versions[i]));
      }
      this.chartOptions.xaxis.categories = categories;
      return this.chartOptions;
    },
    series() {
      return [{ name: this.name, data: this.data }];
    }
  }
};
</script>

<template>
  <q-card>
    <VersionsChart
      name="scores"
      :data="scores()"
      :versions="versions()"
      :key="vlist.length"
    />
  </q-card>
</template>

<script>
import VersionsChart from "../Charts/VersionsChart";
export default {
  components: { VersionsChart },
  name: "BundleChart",
  props: {
    vlist: {
      type: Array,
      required: true
    }
  },
  methods: {
    versions() {
      var versions = [];
      for (var i = 0; i < this.vlist.length; i++) {
        versions.push(this.vlist[i].version);
      }
      return versions;
    },
    scores() {
      var scores = [];
      for (var i = 0; i < this.vlist.length; i++) {
        scores.push(this.vlist[i].score);
      }
      return scores;
    }
  }
};
</script>

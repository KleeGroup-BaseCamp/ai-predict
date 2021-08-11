<template>
  <q-page padding  clickable @click="redirect" class="column items-center">
    <h1 class="text-h3 text-white"> Vertigo AI Manager </h1>
    <div class="row items-center text-white">
      <h2 class="col-5 welcome-figure">{{ count() }}</h2>
      <div class="col text-h6">bundles deployed</div>
    </div>
    <div class=" row items-center text-white">
      <h2 class="col-5 welcome-figure">{{ countActive() }}</h2>
      <div class="col text-h6">bundles active</div>
    </div>
  </q-page>
</template>

<script>
import axios from "axios";

export default {
  name: 'WelcomePage',
  data() {
    return {
      bundles : [],
      input_bundles : []
    }
  },
  mounted() {
    axios.get("/api/bundles/").then(response => {
      this.input_bundles = response.data;
      this.bundles = response.data;
    });
  },

  methods: {
    redirect() {
      this.$router.push('/home');
    },
    count() {
      return this.bundles.length;
    },

    countActive() {
      var count = 0;
      for (var i = 0; i < this.bundles.length; i++) {
        if (this.bundles[i].active) {
          count += 1;
        }
      }
      return count;
    },
  }
}
</script>

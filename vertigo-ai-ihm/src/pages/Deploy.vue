<template>
  <q-page padding>
    <div class="row justify-center">
      <q-card class="col-4">
        <q-card-section>
          <div class="text-h6 text-primary">Import a bundle zip</div>
        </q-card-section>
        <q-separator color="accent" inset />
        <q-card-section class="row justify-center">
          <q-uploader
            class=""
            url="http://127.0.0.1:8000/deploy/"
            @failed="fileFailed"
            color="primary"
            flat
            bordered
          />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script>
export default {
  name: 'Deploy',
  data() {
    return {
    };
  },
  methods: {
    fileFailed(info){
      const response = JSON.parse(info.xhr.response)
      this.$q.notify({
        message: response.error,
        color: "negative"
      });
    }
  }
};
</script>

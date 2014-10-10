function buildNavBar() {
  $('article section').each(function() {
    var elem = $('<li>')
      .append($('<a>')
        .attr({
          href: '#' + this.id})
        .text(this.children[0].textContent));
    $('#sidebar').append(elem);
  });
}
buildNavBar();

$('#navbar').affix({
  offset: {
    top: 271,
    bottom: 0
  }
});

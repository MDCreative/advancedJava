$(document).ready(function(){
    $('.fadeOut').delay(1000).fadeOut(500);
    
    $('#wordsTable tfoot th').each( function () {
        if($(this).index()>0 && $(this).index()<4){
           
        
            var title = $('#wordsTable thead th').eq( $(this).index() ).text();
            $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
        }
    } );
    var table = $('#wordsTable').DataTable();
 
    // Apply the search
    table.columns().every( function () {
        var that = this;
        $( 'input', this.footer() ).on( 'keyup change', function () {
            that
                .search( this.value )
                .draw();
        } );
    } );
    
    modal = "<div class=\"ui modal\">\n"
                    + "  <i class=\"close icon\"></i>\n"
                    + "  <div class=\"header\">\n"
                    + "    Are you sure?\n"
                    + "  </div>\n"
                    + "  <div class=\"content\">\n"
                    + "    <div class=\"description\">\n"
                    + "      Are you sure you want to delete this?\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "  <div class=\"actions\">\n"
                    + "    <div class=\"ui button actions positive\">Yes</div>\n"
                    + "    <div class=\"ui button actions negative\">No</div>\n"
                    + "  </div>\n"
                    + "</div>";
    
    $(document).on("click", ".removeButton", function(e){
       console.log('here');
       e.preventDefault();
       e.stopImmediatePropagation();
       console.log(modal);
       $('.ui.modal').remove();
       $(document.body).append(modal);
       $('.ui.modal').modal('show').modal('set active').modal({
           closable: false,
           onApprove: function(){
               window.location.href = e.target.parentElement.href;  
           },
           onDeny: function(){
               $('.ui.modal').modal('hide');
               $('.ui.modal').removeClass("hidden");
               $('.ui.modal').transition('stop');
           }
        });
       
       
       
    });
});

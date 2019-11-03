( function() {
    CKEDITOR.plugins.add( 'keystrokes', {
        init: function( editor ) {
            editor.setKeystroke( CKEDITOR.CTRL + 83 /** S */ , 'saveDoc' );
            editor.addCommand( 'saveDoc', {
                exec: function( editor ) {
                	saveDoc();
                }
            } );
            editor.setKeystroke( CKEDITOR.CTRL + 74 /** J */ , 'historyPrev' );
            editor.addCommand( 'historyPrev', {
                exec: function( editor ) {
                	historyPrev();
                }
            } );
            editor.setKeystroke( CKEDITOR.CTRL + 75 /** K */ , 'historyNext' );
            editor.addCommand( 'historyNext', {
                exec: function( editor ) {
                	historyNext();
                }
            } );
        }
    });
} )();
$(document).ready(function ()
{
    $('#hotdog').popover();
    $('#chipotle').popover();
    $('#chickfila').popover();
    $('#port-1').popover();
    $('#pizza-1').popover();
    $('#subway').popover();
    $('#kfc').popover();
    $('#flame').popover();
    $('#port-2').popover();
    $('#pizza-2').popover();
    $('#taq').popover();
    $('#smoothie').popover();
    $('#pickup').popover();
    $('#carls').popover();

    $("#submit").click(function ()
    {
        submitOrder();
    });


    $('#grade').change(function ()
    {
        checkForFieldTrip();
    });

    /**
     * hide the "Add another" button for now....
     */
    $('#another').hide();
});

function submitOrder()
{
    var name = $('#name').val();
    //console.log("name: " + name);
    if (name == "")
    {
        alert("Please Enter a Name");
        $('#name').focus();
        return false;
    }

    var grade = $('#grade').val();
    //console.log("grade: " + grade);
    if (grade == "Select Grade")
    {
        alert("Please Select a Grade");
        $('#grade').focus();
        return false;
    }

    if ($('#agreement').is(":checked"))
    {
        //console.log("it's checked");
    }
    else
    {
        //console.log("it's NOT checked");
        alert("Please accept the Agreement before submitting your Hot Lunch Order");
        $('#agreement').focus();
        return false;
    }

    //var str = $('#lunch-form').serialize();
    //console.log("str: " + str);

    $.ajax(
        {
            type: 'POST',
            url: '/dispatcher/addLunchOrder',
            cache: false,
            async: false,
            data: $('#lunch-form').serialize(),
            dataType: "json",
            success: function (result)
            {
                if (result != null)
                {
                    window.location.assign('lunch-confirm.html');
                }
                else
                {
                    alert("We were unable to save your Hot Lunch Order, please try again");
                }
            }
        });
}

function checkForFieldTrip()
{
    var grade = $('#grade').val();
    if (grade == "6")
    {
        ////console.log("grade 6 selected...");
        //$('.field-trip').css('display', 'none');
        $('.field-trip').hide();
    }
    else
    {
        //console.log("grade 6 NOT selected...");
        $('.field-trip').show();
    }


}

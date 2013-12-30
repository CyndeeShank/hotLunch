$(document).ready(function ()
{
    $('#submit').click(function ()
    {
        getOrderInfo();
    });

    $('#print-info').hide();

    // set the typeahead name field
    setUpTypeAhead();
});

function getOrderInfo()
{
    var name = $('#name').val();
    //console.log("name: " + name);
    if (name == "")
    {
        alert("Please Enter a Name");
        $('#name').focus();
        return false;
    }

    var month = $('#month').val();
    if (month == "Select Month")
    {
        alert("Please Select a Month");
        $('#month').focus();
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

    $.ajax(
            {
                type: 'POST',
                url: '/dispatcher/getLunchOrder',
                data: {month: month, name: name, grade: grade},
                cache: false,
                async: false,
                dataType: "json",
                success: function (result)
                {
                    if (result != null)
                    {
                        buildLunchOrderTable(result);
                        $('#print-info').show();
                    }
                    else
                    {
                        alert("We were unable retrieve your Hot Lunch Order, please check the Name and Grade and try again");
                    }
                }
            });
}



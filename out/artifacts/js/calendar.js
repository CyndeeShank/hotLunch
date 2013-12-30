$(document).ready(function ()
{
    $("#datepicker").datepicker();

    $("#add-calendar-item").click(function ()
    {
        addItem();
    });

    $("#submit-calendar").click(function ()
    {
        submitCalendar();
    });

});

var calendarMap = {};

function addItem()
{
    var date = $('#datepicker').val();
    var item = $('#item-choice').val();

    console.log("date: " + date);
    console.log("item: " + item);

    // add the item to the calendarMap object
    calendarMap[date] = item;

    var str = '<li class="list-group-item">' + date + ' -- ' + item + '</li>';

    $("#calendar-list").append(str);
}

function submitCalendar()
{
    for (var key in calendarMap)
    {
        console.log("key: " + key + " / value: " + calendarMap[key]);
    }
    var month = $('#month-choice').val();

    //addLunchCalendar(@RequestParam String month, @RequestParam Map<Date, LunchItem> calendarMap)
    $.ajax(
        {
            type: 'POST',
            url: '/dispatcher/addLunchCalendar',
            cache: false,
            async: false,
            data: { month: month, calendarMap: calendarMap },
            dataType: "json",
            success: function (result)
            {
                console.log('result: ' + result);
                //$("#result").html(feedResult.message);
            }
        });
}



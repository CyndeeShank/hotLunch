$(document).ready(function ()
{
    $('#hotdog').popover();
    $('#subway').popover();

    $("#submit").click(function ()
    {
        submitRequest();
    });
});


function submitRequest()
{
    var company = $('#companySelect').val();
    if (company == "select")
    {
        alert("Please select a company")
        return false;
    }
    var feedType = $('#feedTypeSelect').val();
    if (feedType == "select")
    {
        alert("Please select a feed type");
        return false;
    }
    var generate = $('#generateSelect').val();
    if (generate == "select")
    {
        alert("Please select Generate or Generate and Submit feeds");
        return false;
    }
    // /console.log("company: " + company);
    //console.log("feedType: " + feedType);
    //console.log("generate: " + generate);

    $.ajax(
            {
                type:'POST',
                url:'/AmazonService/dispatcher/doOnDemandFeed',
                cache:false,
                async:false,
                data:{ company:company, feedType:feedType, generate:generate },
                dataType:"json",
                success:function (feedResult)
                {
                    $("#result").html(feedResult.message);
                }
            });
}

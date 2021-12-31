# Webhook
Jira ConDec provides a webhook sending decision knowledge to a receiver system via a HTTP post request. 
To activate the webhook, do the following steps:

- As a project administrator, navigate to Jira project settings.
- Select "Webhook" on the side-bar under "ConDec Decision Knowledge".
- Insert a receiver URL and a shared secret (for Slack, there is no need to set a shared secret).
- Select the types of elements, which trigger the webhook, if they are created or edited.
- Activate the webhook with the switch on the top of the page
- You can click the test button to send a test post to the given URL.
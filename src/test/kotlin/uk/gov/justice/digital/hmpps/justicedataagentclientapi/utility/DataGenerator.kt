package uk.gov.justice.digital.hmpps.justicedataagentclientapi.utility

import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.JdaRequest
import uk.gov.justice.digital.hmpps.justicedataagentclientapi.service.integration.dto.request.Prompt
import java.util.UUID

class DataGenerator {

  companion object {
    fun buildJdaResponse(): String = """
        {
            "requestId": "01a00033-008e-7732-add3-873fa0ca5ef6",
            "correlationId": "019fcc4c-fff1-71ce-b853-b52f0b52cc72",
            "prompt": {
                "key": "f3cb630a-b9fb-4eb9-8b8c-97e124e02d2b",
                "version": 1
            },
            "responseData": [
                {
                    "item_id": "76304207-b018-4812-a3bf-f294a05347e8",
                    "usual_behaviour_presentation": 3,
                    "risks_and_triggers": 2,
                    "protective_factors": 3,
                    "comment": "Anxious/withdrawn on arrival, history of self-harm but no current intent. Partner/children visits important protective factor. Polite engagement noted.",
                    "confidence_level": "medium",
                    "justifying_spans": [
                        {
                            "text": "he appeared visibly anxious and withdrawn upon arrival",
                            "justifies": "usual_behaviour_presentation"
                        },
                        {
                            "text": "He was polite throughout the conversation and expressed gratitude at the end of the session",
                            "justifies": "usual_behaviour_presentation"
                        },
                        {
                            "text": "He disclosed a previous history of self-harm but stated he has no current thoughts or intentions",
                            "justifies": "risks_and_triggers"
                        },
                        {
                            "text": "Test prisoner expressed a desire to transfer to a facility closer to his hometown so that his partner and children could visit more regularly",
                            "justifies": "protective_factors"
                        },
                        {
                            "text": "Test prisoner confirmed he understood the support options available to him and said he would approach staff if he needed assistance",
                            "justifies": "protective_factors"
                        }
                    ]
                },
                {
                    "item_id": "f83cc2ea-97b1-4bd2-8cb4-475b7d926488",
                    "usual_behaviour_presentation": 0,
                    "risks_and_triggers": 0,
                    "protective_factors": 0,
                    "comment": "Routine visit with no issues or problems raised. No substantive CSIP-relevant information.",
                    "confidence_level": "high",
                    "justifying_spans": []
                }
            ],
            "metaData": {
                "type": "sync",
                "submittedAt": "2026-08-14T12:15:37Z",
                "processedAt": "2026-08-14T12:15:37Z",
                "receivedAt": "2026-08-14T12:15:37Z",
                "completedAt": "2026-08-14T12:16:04Z"
            }
        }
    """.trimIndent()

    fun buildJdaRequest(correlationId: UUID, promptKey: String, version: Int): JdaRequest = JdaRequest(
      correlationId,
      Prompt(promptKey, version),
      ObjectMapper().readTree(
        """
          [
            {
              "item_id": "76304207-b018-4812-a3bf-f294a05347e8",
              "case_note_text": "New Induction  - Test User arrived at Test Prison on 14/03/2025, this is his third time in custody and he appeared visibly anxious and withdrawn upon arrival. He stated he believed he should be on an enhanced CSRA level but has been placed on standard. Test User expressed a desire to transfer to a facility closer to his hometown so that his partner and children could visit more regularly. He disclosed a previous history of self-harm but stated he has no current thoughts or intentions. Test User mentioned he has some historic links to a local group but was unable to identify any known conflicts within this establishment. Test User confirmed he understood the support options available to him and said he would approach staff if he needed assistance. He was polite throughout the conversation and expressed gratitude at the end of the session."
            },
            {
              "item_id": "33787111-c923-4e5e-ad92-eba8f2e122e8",
              "case_note_text": "During this evening's welfare check, Test Prisoner was in a very distressed state and was unable to engage in any meaningful conversation regarding his wellbeing or future plans. He was unable to confirm whether he had any immediate intentions to harm himself, and when asked directly whether he could keep himself safe overnight, he gave no clear assurance. Staff discussed the possibility of increased monitoring with the on-call manager. Following a brief MDT consultation via telephone, it was agreed that Marcus should be placed on a two-person watch for the remainder of the night, with a formal review to take place at morning handover. Test Prisoner was informed of this decision and did not object."
            },
            {
              "item_id": "ba5c440b-31b8-411d-bbb8-139576e4b5ad",
              "case_note_text": "Test Prisoner  was relocated to the Separation and Care Unit earlier today following concerns raised overnight. He declined all activities offered during the morning regime including exercise and association."
            },
            {
              "item_id": "33ac889f-8bab-4a39-9cf3-28e9d9c8ad95",
              "case_note_text": "Prior to meeting with Mr. Other, I reviewed his recent case notes to prepare for our first key work session together. I noted he has received two warnings in recent weeks - one related to an altercation with another resident and one relating to possession of unauthorised items. He is currently without employment and his ACCT was opened recently following a self-harm disclosure. I met with Mr. Other in his cell and introduced myself as his allocated keyworker and explained the purpose of the session. He initially said everything was fine and seemed reluctant to engage, however after I clarified what key work involves he agreed to participate. His cell was in a poor state of cleanliness. I asked him about the recent warnings and he explained that tensions had arisen with another resident over a misunderstanding, and that the second incident was due to taking items he felt were owed to him. I encouraged him to approach staff in future rather than taking matters into his own hands. ACTION PLAN: 1. Reapply for employment on the wing. 2. Improve cell cleanliness."
            },
            {
              "item_id": "85a964c3-875b-4ff4-a54a-a528ce03454a",
              "case_note_text": "On 22/05/2025 at approximately 11:30, Mr. Other approached me near the servery on B wing and requested to speak about a personal property matter. He stated that several items of clothing had gone missing following a cell search carried out the previous week. I explained that I was not present during that search but that I would look into the matter and follow up with him. Mr. Other became increasingly agitated and began raising his voice, accusing staff of stealing from him. He used threatening and offensive language toward me directly. I activated my body-worn camera and asked him calmly to return to his cell while the matter was investigated. Mr. Other became physically confrontational. A colleague responded to my request for assistance and together we guided Mr. Other back to his cell using appropriate restraint techniques. He was secured in his cell without further incident. Mr. Other will be placed on report for threatening behaviour and failure to comply with a lawful instruction."
            }
          ]
        """.trimIndent(),
      ),
    )
  }
}
